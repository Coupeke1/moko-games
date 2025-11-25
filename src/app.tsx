import GamePage from "@/routes/game/gamePage.tsx"
import {BrowserRouter, Route, Routes} from "react-router"

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/game/:id" element={<GamePage/>}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App;