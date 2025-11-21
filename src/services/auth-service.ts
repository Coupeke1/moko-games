import axios from 'axios'

export function addToken(token: string | undefined) {
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    else {
        removeToken()
    }
}

export function removeToken() {
    delete axios.defaults.headers.common['Authorization']
}