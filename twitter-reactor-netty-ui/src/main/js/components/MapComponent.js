/* eslint-disable max-len */
/* global fetch, window */
import React, {Component} from 'react';
import {StaticMap} from 'react-map-gl';
import {connect} from 'react-redux';
import DeckGL, {IconLayer, TextLayer} from 'deck.gl';
import {startStream, stopStream} from '../actions/index';

// Set your mapbox token here
const MAPBOX_TOKEN = process.env['MapboxAccessToken']; // eslint-disable-line
// mapbox style file path
const MAPBOX_STYLE = 'mapbox://styles/mapbox/dark-v9';

export const INITIAL_VIEW_STATE = {
    longitude: -35,
    latitude: 36.7,
    zoom: 1.8,
    maxZoom: 20,
    pitch: 0,
    bearing: 0
};

export class MapComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {};

    if (!window.demoLauncherActive) {
      this.props.startStream();
    }
  }

  componentWillUnmount() {
    this.props.stopStream();
  }

  // _loadData() {
  //   fetch(DATA_URL)
  //     .then(resp => resp.json())
  //     .then(resp => {
  //       // each entry in the data object contains all tweets posted at that second
  //       const data = Array.from({ length: SECONDS_PER_DAY }, () => []);
  //       resp.forEach(val => {
  //         const second = parseInt(val.time, 10) % SECONDS_PER_DAY;
  //         data[second].push(val);
  //       });
  //       this.setState({ data });
  //       window.requestAnimationFrame(this._animateData.bind(this));
  //     });
  // }

  // _animateData() {
  //   const { data } = this.state;
  //   const now = Date.now();
  //   const getSecCeil = ms => Math.ceil(ms / 1000, 10) % SECONDS_PER_DAY;
  //   const getSecFloor = ms => Math.floor(ms / 1000, 10) % SECONDS_PER_DAY;
  //   const timeWindow = [
  //     getSecCeil(now - TIME_WINDOW * 1000),
  //     getSecFloor(now + TIME_WINDOW * 1000)
  //   ];
  //   if (data) {
  //     let dataSlice = [];
  //     for (let i = timeWindow[0]; i <= timeWindow[1]; i++) {
  //       if (i >= 0 && i < data.length) {
  //         const slice = data[i].map(val => {
  //           const offset = Math.abs(getSecFloor(now) + (now % 1000) / 1000 - i) / TIME_WINDOW;
  //           // use non-linear function to achieve smooth animation
  //           const opac = Math.cos((offset * Math.PI) / 2);
  //           const color = [...TEXT_COLOR, opac * 255];
  //           return Object.assign({}, val, { color }, { size: 12 * (opac + 1) });
  //         });
  //         dataSlice = [...dataSlice, ...slice];
  //       }
  //     }
  //     this.setState({ dataSlice });
  //   }

  //   this._animationFrame = window.requestAnimationFrame(this._animateData.bind(this));
  // }

  _renderLayers() {
      const {
          data,
          iconMapping = 'data/location-icon-mapping.json',
          iconAtlas = 'data/location-icon-atlas.png',
          showCluster = true,
          viewState
      } = this.props;

      const layerProps = {
          data,
          pickable: false,
          wrapLongitude: true,
          getPosition: d => d.location,
          iconAtlas,
          iconMapping,
          // onHover: this._onHover,
          // onClick: this._onClick,
          sizeScale: 60
      };

      const size = viewState ? Math.min(Math.pow(1.5, viewState.zoom - 10), 1) : 0.7;

      // const layer = showCluster
      //     ? new IconClusterLayer({...layerProps, id: 'icon-cluster'})
      //     :
      const layer = new IconLayer({
              ...layerProps,
              id: 'icon',
              getIcon: d => 'marker',
              getSize: size
          });

      return [layer];
    // return [
    //   new TextLayer({
    //     id: 'hashtag-layer',
    //     data: this.props.data,
    //     sizeScale: 4,
    //     getText: d => d.content,
    //     getPosition: d => d.location,
    //     getColor: d => [...TEXT_COLOR, 255],
    //     getSize: d => 24
    //   })
    // ];
  }

  render() {
    const { viewState, controller = true, baseMap = true } = this.props;

    return (
      <DeckGL
        layers={this._renderLayers()}
        initialViewState={INITIAL_VIEW_STATE}
        viewState={viewState}
        controller={controller}
        // parameters={{
        //   blendFunc: [GL.SRC_ALPHA, GL.ONE, GL.ONE_MINUS_DST_ALPHA, GL.ONE],
        //   blendEquation: GL.FUNC_ADD
        // }}
      >
        {baseMap && (
          <StaticMap
            reuseMaps
            mapStyle={MAPBOX_STYLE}

            preventStyleDiffing={true}
            mapboxApiAccessToken={MAPBOX_TOKEN}
          />
        )}
      </DeckGL>
    );
  }
}

const mapStateToProps = (state) => ({
    data: state.map.data,
}); // eslint-disable-line object-curly-newline

const mapDispatchToProps = (dispatch) => ({
    dispatch,
    startStream: () => dispatch(startStream()),
    stopStream: () => dispatch(stopStream()),
});


export default connect(mapStateToProps, mapDispatchToProps)(MapComponent);