import React, {Component} from 'react';


export class ScoreboardEntry extends Component {
    constructor(props) {
      super(props);
      this.state = {};
    }
  
    render() {
      const { author, index } = this.props;
      let str = `${index + 1}. ${author}`;
      return (
          <div>
              <p>{str}</p>
          </div>
        );
    }
  }
  
  
  export default (ScoreboardEntry);  