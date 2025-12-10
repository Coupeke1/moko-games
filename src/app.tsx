import GamePage from "@/routes/game/game"
import {BrowserRouter, Route, Routes} from "react-router"
import {QueryClient, QueryClientProvider} from "@tanstack/react-query"
import Auth from "@/components/auth.tsx"

const queryClient = new QueryClient();

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <Auth />
            <BrowserRouter basename="/checkers">
                <Routes>
                    <Route path="/game/:gameId" element={<GamePage/>}/>
                    <Route path="/" element={<GamePage/>}/>
                </Routes>
            </BrowserRouter>
        </QueryClientProvider>
    )
}

export default App;