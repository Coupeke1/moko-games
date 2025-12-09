export const Type = {
    Info: "INFO",
    MinorUpdate: "MINOR_UPDATE",
    MajorUpdate: "MAJOR_UPDATE",
    Devlog: "DEVLOG",
} as const;

export type Type = (typeof Type)[keyof typeof Type];

export function format(type: Type | string) {
    return `${type.charAt(0)}${type.slice(1).toLowerCase().replaceAll("_", " ")}`;
}
