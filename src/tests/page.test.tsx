import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import Page from "@/components/layout/page.tsx"; // adjust the path if needed

describe("Page component", () => {
  it("renders children correctly", () => {
    render(
      <Page>
        <div data-testid="child">Hello World</div>
      </Page>
    );

    const child = screen.getByTestId("child");
    expect(child).toBeInTheDocument();
    expect(child).toHaveTextContent("Hello World");
  });
});