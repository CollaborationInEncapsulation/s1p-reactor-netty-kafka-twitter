import React from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware, compose } from 'redux';
import { createEpicMiddleware } from 'redux-observable';
import MapComponent from './components/MapComponent';
import createEpic from './epics';
import createReducer   from './reducers';

const epicMiddleware = createEpicMiddleware();

/**
 * The redux state store, built with the Epic middleware.
 */
const store = createStore(
    createReducer(),
    {},
    compose(applyMiddleware(epicMiddleware))
);

epicMiddleware.run(createEpic());

const container = document.getElementById('app');

render(
    <Provider store={store}>
        <MapComponent/>
    </Provider>,
    container
);