export function format(value: string) {
    return `${value.charAt(0)}${value.slice(1).toLowerCase().replaceAll("_", " ")}`;
}

export function camel(value: string): string {
    return value
        .replace(/([a-z0-9])([A-Z])/g, "$1 $2")
        .replace(/([A-Z]+)([A-Z][a-z])/g, "$1 $2")
        .replace(/^./, (char) => char.toUpperCase());
}

export function slug(value: string) {
    return value.toLowerCase().replaceAll("_", "-");
}

export function date(value: Date | string | number): string {
    const date = new Date(value);
    if (isNaN(date.getTime())) throw new Error("Invalid date");

    const day = date.getDate();
    const year = date.getFullYear();

    const months = [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sept",
        "Oct",
        "Nov",
        "Dec",
    ];

    const month = months[date.getMonth()];
    return `${day} ${month} ${year}`;
}

export function relativeDate(value: Date | string | number): string {
    const date = new Date(value);
    if (isNaN(date.getTime())) throw new Error("Invalid date");

    const now = new Date();
    const diffMs = now.getTime() - date.getTime();

    const seconds = Math.floor(diffMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (seconds < 60) return "just now";
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    if (days < 365) {
        const years = days / 365;
        const rounded = Math.round(years * 2) / 2;
        return `${rounded} year${rounded !== 1 ? "s" : ""} ago`;
    }

    const years = Math.floor(days / 365);
    return `${years} year${years !== 1 ? "s" : ""} ago`;
}
