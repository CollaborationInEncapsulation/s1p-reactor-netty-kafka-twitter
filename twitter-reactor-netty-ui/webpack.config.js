// NOTE: To use this example standalone (e.g. outside of deck.gl repo)
// delete the local development overrides at the bottom of this file

// avoid destructuring for older Node version support
const resolve = require('path').resolve;
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

const CONFIG = {

  devServer: {
    contentBase: "./dist",
    compress: true,
    port: 8080
  },

  mode: 'development',

  entry: {
    app: resolve('./src/main/js/index.js')
  },

  output: {
    filename: 'main.js',
    path: resolve(__dirname, 'build/resources/main/static'),
    chunkFilename: '[name].bundle.js',
    publicPath: '/'
  },

  module: {
    rules: [
      {
        // Compile ES2015 using buble
        test: /\.js$/,
        loader: 'buble-loader',
        include: [resolve('.')],
        exclude: [/node_modules/],
        options: {
          objectAssign: 'Object.assign'
        }
      }
    ]
  },

  resolve: {
    alias: {
      // From mapbox-gl-js README. Required for non-browserify bundlers (e.g. webpack):
      'mapbox-gl$': resolve('./node_modules/mapbox-gl/dist/mapbox-gl.js')
    }
  },

  // Optional: Enables reading mapbox token from environment variable
  plugins: [
    new HtmlWebpackPlugin({
      template: './src/main/js/template.html'
    }),
    new CopyWebpackPlugin([{
      from: './src/main/js/data',
      to: 'data'
    }]),
    new webpack.EnvironmentPlugin(['MapboxAccessToken'])
  ]
};

// This line enables bundling against src in this repo rather than installed module
module.exports = env => CONFIG;
