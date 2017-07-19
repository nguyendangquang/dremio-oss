/*
 * Copyright (C) 2017 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, { Component, PropTypes } from 'react';
import ReactDOM from 'react-dom';
import Radium from 'radium';

import DragTarget from 'components/DragComponents/DragTarget';

import { areaWrap, getDragAreaStyle, dragAreaText, columnWrap } from 'components/Aggregate/aggregateStyles';

@Radium
class ExploreDragArea extends Component {
  static propTypes = {
    onDrop: PropTypes.func,
    dragType: PropTypes.string,
    emptyDragAreaText: PropTypes.string,
    isDragged: PropTypes.bool,
    dragContentStyle: PropTypes.object,
    children: PropTypes.node,
    dataQa: PropTypes.string
  };

  componentDidUpdate(prevProps) {
    const newCount = React.Children.count(this.props.children);
    const prevCount = React.Children.count(prevProps.children);
    const wrapper = ReactDOM.findDOMNode(this.wrapper);
    if (newCount > prevCount && wrapper.scrollHeight > wrapper.clientHeight) {
      $(wrapper).animate({
        scrollTop: wrapper.scrollHeight
      }, 300);
    }
  }

  renderEmpty() {
    const { emptyDragAreaText } = this.props;
    return <span className='empty-text' style={dragAreaText}>
      {emptyDragAreaText}
    </span>;
  }

  render() {
    const { isDragged, dragType, onDrop, dragContentStyle, children, dataQa } = this.props;

    const isEmpty = React.Children.count(children) === 0;
    const dragAreaStyle = getDragAreaStyle(isDragged, isEmpty);
    const columnStyle = !isEmpty ? columnWrap : {};

    return (
      <div className='drag-area' data-qa={dataQa} style={areaWrap}>
        <DragTarget
          dragType={dragType}
          canDropOnChild
          onDrop={onDrop}>
          <div ref={(wrapper) => {
            this.wrapper = wrapper;
          }} style={[dragAreaStyle, dragContentStyle]}>
            <div style={[columnStyle]}>
              {isEmpty ? this.renderEmpty() : children}
            </div>
          </div>
        </DragTarget>
      </div>
    );
  }
}

export default ExploreDragArea;