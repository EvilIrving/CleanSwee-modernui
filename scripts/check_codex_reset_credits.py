#!/usr/bin/env python3
import argparse
import json
import os
import sys
from datetime import datetime, timedelta, timezone
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


RESET_CREDITS_URL = "https://chatgpt.com/backend-api/wham/rate-limit-reset-credits"
DEFAULT_AUTH_FILE = Path(os.environ.get("CODEX_HOME", Path.home() / ".codex")) / "auth.json"
CHINA_TIMEZONE = timezone(timedelta(hours=8))


def load_access_token(auth_file: Path) -> str:
    try:
        auth = json.loads(auth_file.expanduser().read_text(encoding="utf-8"))
    except FileNotFoundError:
        raise RuntimeError(f"找不到 Codex 登录文件：{auth_file}") from None
    except json.JSONDecodeError as exc:
        raise RuntimeError(f"Codex 登录文件不是有效 JSON：{auth_file}：{exc}") from None

    token = auth.get("tokens", {}).get("access_token")
    if not token:
        raise RuntimeError(f"Codex 登录文件里没有 access token：{auth_file}")
    return token


def fetch_reset_credits(access_token: str, timeout: int) -> dict:
    request = Request(
        RESET_CREDITS_URL,
        headers={
            "Accept": "application/json",
            "Authorization": f"Bearer {access_token}",
            "Origin": "https://chatgpt.com",
            "Referer": "https://chatgpt.com/",
            "User-Agent": "codex-reset-credits-local/1.0",
        },
    )

    try:
        with urlopen(request, timeout=timeout) as response:
            return json.load(response)
    except HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="replace").strip()
        message = f"请求失败：HTTP {exc.code}"
        if detail:
            message = f"{message}：{detail}"
        raise RuntimeError(message) from None
    except URLError as exc:
        raise RuntimeError(f"请求失败：{exc.reason}") from None


def parse_time(value: str) -> datetime:
    return datetime.fromisoformat(value.replace("Z", "+00:00"))


def format_duration(seconds: float) -> str:
    seconds = int(seconds)
    sign = "-" if seconds < 0 else ""
    seconds = abs(seconds)
    days, seconds = divmod(seconds, 86_400)
    hours, seconds = divmod(seconds, 3_600)
    minutes, seconds = divmod(seconds, 60)
    return f"{sign}{days}天 {hours}小时 {minutes}分钟 {seconds}秒"


def format_china_time(value: datetime) -> str:
    return value.astimezone(CHINA_TIMEZONE).strftime("%Y-%m-%d %H:%M:%S")


def build_summary(data: dict, include_details: bool) -> dict:
    credits = data.get("credits")
    available_count = data.get("available_count")

    if available_count is None and isinstance(credits, list):
        available_count = len(credits)
    if available_count is None:
        available_count = 0

    result = {"available_count": available_count}
    if not include_details:
        return result

    now = datetime.now(timezone.utc)
    detailed_credits = []
    for index, credit in enumerate(credits or [], start=1):
        expires_at = parse_time(credit["expires_at"])
        reminder_1_day = expires_at - timedelta(days=1)
        reminder_1_hour = expires_at - timedelta(hours=1)
        detailed_credits.append(
            {
                "index": index,
                "remaining": format_duration(expires_at.timestamp() - now.timestamp()),
                "expires_at_utc": expires_at.isoformat(timespec="seconds"),
                "expires_at_china": expires_at.astimezone(CHINA_TIMEZONE).isoformat(
                    timespec="seconds"
                ),
                "reminders": [
                    {
                        "label": "过期前 1 天",
                        "at_utc": reminder_1_day.isoformat(timespec="seconds"),
                        "at_china": reminder_1_day.astimezone(CHINA_TIMEZONE).isoformat(
                            timespec="seconds"
                        ),
                    },
                    {
                        "label": "过期前 1 小时",
                        "at_utc": reminder_1_hour.isoformat(timespec="seconds"),
                        "at_china": reminder_1_hour.astimezone(CHINA_TIMEZONE).isoformat(
                            timespec="seconds"
                        ),
                    },
                ],
            }
        )
    result["credits"] = detailed_credits
    return result


def print_text(result: dict) -> None:
    print(f"Codex 重置次数：{result['available_count']}")
    for credit in result.get("credits", []):
        expires_at = parse_time(credit["expires_at_china"])
        print(f"第 {credit['index']} 次重置：")
        print(f"  剩余时间：{credit['remaining']}")
        print(f"  过期时间：{format_china_time(expires_at)}（中国时间）")
        for reminder in credit["reminders"]:
            reminder_at = parse_time(reminder["at_china"])
            print(f"  {reminder['label']}提醒：{format_china_time(reminder_at)}（中国时间）")


def main() -> int:
    parser = argparse.ArgumentParser(
        description="本地查询 Codex reset credits，只显示可用次数，不显示过期时间。"
    )
    parser.add_argument(
        "--auth-file",
        type=Path,
        default=DEFAULT_AUTH_FILE,
        help=f"Codex auth.json 路径，默认：{DEFAULT_AUTH_FILE}",
    )
    parser.add_argument("--json", action="store_true", help="输出 JSON，便于脚本处理。")
    parser.add_argument(
        "--details",
        action="store_true",
        help="显示每一次重置的剩余时间、过期时间和提醒时间。",
    )
    parser.add_argument("--timeout", type=int, default=20, help="请求超时时间，单位秒。")
    args = parser.parse_args()

    try:
        result = build_summary(
            fetch_reset_credits(load_access_token(args.auth_file), args.timeout),
            include_details=args.details,
        )
    except RuntimeError as exc:
        print(str(exc), file=sys.stderr)
        return 1

    if args.json:
        print(json.dumps(result, ensure_ascii=False, indent=2))
    else:
        print_text(result)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
