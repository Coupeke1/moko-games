import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import ErrorState from '@/components/state/error.tsx';

describe('ErrorState', () => {
    it('renders without crashing', () => {
        render(<ErrorState />);
        expect(true).toBe(true); // always passes
    });
});