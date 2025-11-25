export default function idIsValidCheck(id: string) {
    if (id.length === 36) return;
    throw new Error(`Id '${id}' is not valid!`)
}