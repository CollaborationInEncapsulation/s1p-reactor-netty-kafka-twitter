import { combineReducers } from 'redux';

import { UPDATE_MAP } from '../constants/index';

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

const createReducer = () => combineReducers({
  map: mapReducer,
});

export default createReducer;