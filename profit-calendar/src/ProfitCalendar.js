import React from 'react';
import Calendar from 'react-calendar';
import './ProfitCalendar.css';

const ProfitCalendar = ({ profitData }) => {
    const tileContent = ({ date, view }) => {
        if (view === 'month') {
            const dateString = date.toISOString().split('T')[0];
            const profit = profitData[dateString];

            if (profit !== undefined) {
                const profitClass = profit < 0 ? 'negative' : 'positive';
                return <div className={`profit ${profitClass}`}>{profit}</div>;
            }
        }
        return null;
    };

    return (
        <div>
            <Calendar
                tileContent={tileContent}
            />
        </div>
    );
};

export default ProfitCalendar;
