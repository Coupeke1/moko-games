import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import PlayerRoleBadge from "@/components/player-role-badge.tsx"; // adjust the path if needed

describe("PlayerRoleBadge component", () => {
    it("renders the role when provided", () => {
        render(<PlayerRoleBadge role="X" />);

        const badge = screen.getByText("X");
        expect(badge).toBeDefined();
        expect(badge).toHaveClass("player-role text-5xl font-bold text-x");
    });

    it("renders the correct class for O", () => {
        render(<PlayerRoleBadge role="O" />);

        const badge = screen.getByText("O");
        expect(badge).toBeDefined();
        expect(badge).toHaveClass("player-role text-5xl font-bold text-o");
    });

    it("renders nothing when role is null", () => {
        const { container } = render(<PlayerRoleBadge role={null} />);
        expect(container.firstChild).toBeNull();
    });
});