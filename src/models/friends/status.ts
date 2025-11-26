export const Status = {
    Pending: "PENDING",
    Accepted: "ACCEPTED",
    Rejected: "REJECTED",
    Cancelled: "CANCELLED",
} as const;

export type Status = (typeof Status)[keyof typeof Status];