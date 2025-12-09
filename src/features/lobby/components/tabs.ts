export function getTabs(id: string) {
    return [
        { title: "Players", path: `/lobby/${id}/players` },
        { title: "Settings", path: `/lobby/${id}/settings` },
    ];
}
