export function format(value: string) {
    return `${value.charAt(0)}${value.slice(1).toLowerCase().replaceAll("_", " ")}`;
}

export function slug(value: string) {
    return value.toLowerCase().replaceAll("_", "-");
}
