import { Observable, of } from 'rxjs';
import { zip, map, mergeMap, retryWhen, takeUntil, catchError } from 'rxjs/operators';
import { combineEpics } from 'redux-observable';

import { START_STREAM, STOP_STREAM } from '../constants/index';

import { updateMap } from '../actions/index';

const url = (location.protocol.includes("https") ? "wss" : "ws") + "://" + location.host + "/ws";
const eventSource = new WebSocket(url);
// const url = '/sse';
// const eventSource = new EventSource(url);
const socket$ = Observable.create((observer) => {
    eventSource.onmessage = (e) => {
        console.log(e);
        observer.next(JSON.parse(e.data));
    };
    eventSource.onerror = (e) => {
        if (eventSource.readyState !== EventSource.CONNECTING) {
            observer.error(new Error(e));
        }
    }
});

const websocketTradesEpic = action$ => action$
    .ofType(START_STREAM)
    .pipe(mergeMap(() => socket$.pipe(
        retryWhen(e => e.pipe(zip(Observable.interval(1000)))),
        map((payload) => updateMap(payload)),
        takeUntil(action$.ofType(STOP_STREAM)),
        catchError(() => of({ type: 'ERROR' }))
    )));

export default () => combineEpics(websocketTradesEpic);