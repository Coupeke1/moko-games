import { defineConfig } from 'vitest/config';
import path from 'path';

export default defineConfig({
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'), // important
        },
    },
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: './src/tests/setupTests.ts',
        include: ['src/**/*.{test,spec}.{js,ts,jsx,tsx}'],
        reporters: ['default', 'junit'],
        outputFile: {
            junit: 'test-results/vitest-report.xml'
        }
    }
});