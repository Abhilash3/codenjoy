// vendor
import React, { Component } from 'react';
import { ConnectedRouter } from 'connected-react-router';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/integration/react';
import 'css/style/uui-all.css';
import 'css/fonts/font-awesome/css/font-awesome.css';

// proj
import store, { history, persistor } from './store';
import Routes from './routes/Routes';

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
