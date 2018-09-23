import { combineReducers } from 'redux';

import { UPDATE_MAP } from '../constants/index';

// The initial state of the App
const initialState = JSON.parse(localStorage.getItem("state") || "{\"data\" : [], \"usersScore\" : {}, \"scoreBoard\" : []}");
function mapReducer(state = initialState, action) {
    switch (action.type) {
        case UPDATE_MAP:
            let newScore = Object.assign({}, state.usersScore);
            newScore[action.payload.user] = (newScore[action.payload.user] || 0) + 1;
            let updatedState =  {
                ...state,
                data: [
                    action.payload,
                    ...state.data
                ],
                usersScore: newScore,
                scoreBoard: newScore[action.payload.user] == 1
                ? [
                      ...state.scoreBoard,
                      action.payload.user
                  ]
                : bubbleSort(state.scoreBoard, (a, b) => newScore[b] < newScore[a]),
            };
            localStorage.setItem("state", JSON.stringify(updatedState));
            return updatedState;
            default:
                return state;
    }
}


function bubbleSort(a, comparator) {
    for (var i=a.length-1; i > 0 ; i--) {
        if (comparator(a[i], a[i-1])) {
            var temp = a[i];
            a[i] = a[i-1];
            a[i-1] = temp;
        }
    }

    return a;
}

const createReducer = () => combineReducers({
  map: mapReducer,
});

export default createReducer;