import React, {Component} from 'react';
import {connect} from 'react-redux';

import ScoreboardEntry from './ScoreboardEntry';

export class Scoreboard extends Component {
    constructor(props) {
      super(props);
  
      this.state = {};
    }
  
    render() {
      const { data } = this.props;
  
      return (
            <div className="scoreboard">
                {data.map((author, i) => <ScoreboardEntry author={author} index={i} key={i}></ScoreboardEntry>)}
            </div>
        );
    }
  }

const mapStateToProps = (state) => ({
    data: state.score.data,
}); // eslint-disable-line object-curly-newline

const mapDispatchToProps = (dispatch) => ({
    dispatch,
    startStream: () => dispatch(startStream()),
});

  
export default connect(mapStateToProps, mapDispatchToProps)(Scoreboard);