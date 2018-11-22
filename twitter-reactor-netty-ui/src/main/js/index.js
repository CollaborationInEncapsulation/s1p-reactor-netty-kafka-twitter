import React from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware, compose } from 'redux';
import { createEpicMiddleware } from 'redux-observable';
import MapComponent from './components/MapComponent';
import createEpic from './epics';
import createReducer   from './reducers';
import Scoreboard from './components/Scoreboard';

const epicMiddleware = createEpicMiddleware();

/**
 * The redux state store, built with the Epic middleware.
 */

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = createStore(
    createReducer(),
    {},
    composeEnhancers(applyMiddleware(epicMiddleware))
);



epicMiddleware.run(createEpic());

const container = document.getElementById('app');
let a = {
    id: 77,
    user: "user" + 77,
    content: "tweet",
    tags: ["tags", "tags2"],
    location: [-10.01,  54.10]
  }

render(
    <Provider store={store}>
      <React.Fragment>
        <MapComponent/>,
        <Scoreboard data={[a]}></Scoreboard>
      </React.Fragment>
    </Provider>,
    

    container
);