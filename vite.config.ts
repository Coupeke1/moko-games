import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import { defineConfig } from 'vite'

export default defineConfig({
    plugins: [react(), tailwindcss(),],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: './src/setupTests.ts',
        include: ['src/**/*.{test,spec}.{js,ts,jsx,tsx}'],
        reporters: ['default', 'junit'],
        outputFile: {
            junit: 'test-results/vitest-report.xml'
        }
    }
})
