import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import NavigationMenu from '../components/navigation/menu';
import '@testing-library/jest-dom';

describe('NavigationMenu', () => {
    it('renders the menu title', () => {
        // Render the component
        render(<NavigationMenu open={true} onClose={() => {}} />);

        // Check that the title "Moko" is in the document
        expect(screen.getByText('Moko')).toBeInTheDocument();
    });
});