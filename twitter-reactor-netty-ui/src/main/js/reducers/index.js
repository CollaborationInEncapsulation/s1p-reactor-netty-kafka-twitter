import { combineReducers } from 'redux';

import { UPDATE_MAP, UPDATE_SCORE } from '../constants/index';

// The initial state of the App
const initialState = {
    data: []
};

function mapReducer(state = initialState, action) {
    switch (action.type) {
        case UPDATE_MAP:

            return {
                ...state,
                data: [
                    action.payload,
                    ...state.data
                ]
            };
        default:
            return state;
    }
}

function scoreboardReducer(state = initialState, action) {
    switch (action.type) {
        case UPDATE_MAP:
            return {
                ...state,
                data: [
                    state.data.filter(user => user.user === action.payload.user).length === 0 
                    ? {user: action.payload.user, tweets: 1} :
                    state.data.filter(user => user.user === action.payload.user).map(record => {return {user: record.user, tweets: record.tweets + 1}})[0],
                    ...state.data.filter(user => user.user !== action.payload.user)
                ]
            };
        default:
            return state;
    }
}

const createReducer = () => combineReducers({
  map: mapReducer,
  score: scoreboardReducer
});

export default createReducer;