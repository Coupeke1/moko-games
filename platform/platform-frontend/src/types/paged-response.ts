export interface PagedResponse<T> {
    items: T[];
    page: number;
    size: number;
    last: boolean;
}
