import GamePage from "@/routes/game/game.tsx"
import {BrowserRouter, Route, Routes} from "react-router"
import Auth from "@/components/auth.tsx";

function App() {
    return (
        <BrowserRouter>
            <Auth />

            <Routes>
                <Route path="/game/:id" element={<GamePage/>}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App;