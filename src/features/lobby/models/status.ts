export const Status = {
    Open: "OPEN",
    Closed: "CLOSED",
    Started: "STARTED",
    Finished: "FINISHED",
    Cancelled: "CANCELLED"
} as const;

export type Status = (typeof Status)[keyof typeof Status];