export function format(value: string) {
    return `${value.charAt(0)}${value.slice(1).toLowerCase().replaceAll("_", " ")}`;
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
