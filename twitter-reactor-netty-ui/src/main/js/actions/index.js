import { START_STREAM, STOP_STREAM, UPDATE_MAP, UPDATE_SCORE } from '../constants/index'


export function startStream() {
    return {
        type: START_STREAM,
    };
}

export function stopStream() {
    return {
        type: STOP_STREAM,
    };
}

export function updateMap(data) {
    return {
        type: UPDATE_MAP,
        payload: data
    };
}