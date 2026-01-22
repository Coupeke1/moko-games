export default function ErrorState({ msg = 'Something went wrong' }) {
    return (
        <div className="grow flex items-center justify-center">
            <div className="bg-bg-2 border border-fg-2/30 rounded-xl p-6 text-center">
                <p className="text-fg font-semibold mb-2">Oops</p>
                <p className="text-fg-2 text-sm">{msg}</p>
            </div>
        </div>
    );
}