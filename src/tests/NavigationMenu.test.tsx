import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import Grid from "@/components/layout/grid/grid.tsx";

describe("Grid component", () => {
    it("renders its children correctly", () => {
        render(
            <Grid>
                <div>Test Content</div>
            </Grid>
        );

        // Check if the child text is rendered
        expect(screen.getByText("Test Content")).toBeInTheDocument();
    });
});