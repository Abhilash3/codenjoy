// vendor
import React, { Component } from 'react';
import { connect } from 'react-redux';

// proj
import { setSelectedDay, fetchRating } from '../../redux/board';
import { BattleFrame, DaysPanel, RatingTable } from '../../components';

// own
import Styles from './styles.module.css';

// TODO set env variables
const period = {
    start: '2019-01-01T10:00:00.000Z',
    end:   '2019-01-16T10:00:00.000Z',
};

class BoardContainer extends Component {
    componentDidMount() {
        this.props.fetchRating(this.props.selectedDay);
    }

    render() {
        const { setSelectedDay } = this.props;
        const { selectedDay, rating } = this.props;

        return (
            <>
                <DaysPanel selectedDay={ selectedDay } onDaySelect={ setSelectedDay } period={ period } />
                <div className={ Styles.wrapper }>
                    <div className={ Styles.frame }>
                        <BattleFrame />
                    </div>
                    <div className={ Styles.rating }>
                        <RatingTable rating={ rating } />
                    </div>
                </div>
            </>
        );
    }
}

const mapStateToProps = state => ({
    selectedDay: state.board.selectedDay,
    rating:      state.board.rating,
});

export default connect(
    mapStateToProps,
    { setSelectedDay, fetchRating },
)(BoardContainer);
