// vendor
import React, { Component } from 'react';
import Iframe from 'react-iframe';

class BattleFrameHandler extends Component {
    render() {
        const { participant } = this.props;

        // TODO fix when iframe becomes available
        return participant ? (
            <div>
                { participant }
                <br />
                <Iframe
                    url='http://codenjoy.juja.com.ua/codenjoy-contest/board/player/dsdst@gmail.com?only=true'
                    width='400px'
                    height='400px'
                    id='myId'
                    display='initial'
                    position='relative'
                    allowFullScreen
                />
            </div>
        ) : null;
    }
}

export const BattleFrame = BattleFrameHandler;
