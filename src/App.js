// vendor
import React, { Component } from 'react';
import { ConnectedRouter } from 'connected-react-router';
import { Provider } from 'react-redux';
import 'reset-css'
import { PersistGate } from 'redux-persist/integration/react';
import { library } from '@fortawesome/fontawesome-svg-core'
import { faTimesCircle, faCheckCircle } from '@fortawesome/free-solid-svg-icons'

// proj
import store, { history, persistor } from './store';
import Routes from './routes/Routes';

library.add(faTimesCircle, faCheckCircle);

class App extends Component {
    render() {
        return (
            <Provider store={ store }>
                <PersistGate loading={ null } persistor={ persistor }>
                    <ConnectedRouter history={ history }>
                        <Routes />
                    </ConnectedRouter>
                </PersistGate>
            </Provider>
        );
    }
}

export default App;
