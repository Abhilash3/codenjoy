// vendor
import React, { Component } from 'react';
import { Switch, Route, Redirect } from 'react-router-dom';

// proj
import {
    LoginPage,
    BoardPage,
    ExceptionPage,
    RegisterPage,
    RulesPage,
    HomePage,
    PrivacyPolicyPage,
} from '../pages';
import { book } from './index.js';

export default class Public extends Component {
    render() {
        return (
            <Switch>
                <Route exact component={ HomePage } path={ book.home } />
                <Route exact component={ RulesPage } path={ book.rules } />
                <Route exact component={ BoardPage } path={ book.board } />
                <Route exact component={ LoginPage } path={ book.login } />
                <Route exact component={ RegisterPage } path={ book.register } />
                <Route exact component={ PrivacyPolicyPage } path={ book.privacyPolicy } />
                <Route
                    component={ ExceptionPage }
                    path={ book.exceptionStatusCode }
                />
                <Redirect to={ book.login } />
            </Switch>
        );
    }
}
