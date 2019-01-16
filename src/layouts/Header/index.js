// vendor
import React, { PureComponent } from 'react';
import { NavLink, withRouter } from 'react-router-dom';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import logo from './game-logo.png'

// proj
import { book } from '../../routes';
import { getGameConnectionString } from '../../utils';

// own
import Styles from './styles.module.css';

class HeaderComponent extends PureComponent {
    render() {
        const { server, logout, email, code } = this.props;

        return (
            <header>
                <div className={ Styles.container }>
                    <div className={ Styles.logoContainer }>
                        <img className={ Styles.logo } src={ logo } alt="" />
                        EPAM BOT CHALLENGE
                    </div>

                    { server && (
                        <div className={ Styles.serverInfo }>
                            <div className={ Styles.serverName }>
                                Сервер: { server }
                            </div>
                            <CopyToClipboard
                                text={ getGameConnectionString(
                                    server,
                                    code,
                                    email,
                                ) }
                            >
                                <button className={ Styles.copyButton }>
                                    Copy
                                </button>
                            </CopyToClipboard>
                        </div>
                    ) }

                    <ul>
                        { /* <li className={ Styles.navItem }>
                            <NavLink to={ book.board }>Головна</NavLink>
                        </li> */ }
                        <li>
                            <NavLink activeClassName={ Styles.activeMenu } to={ book.board }>Трансляція</NavLink>
                        </li>

                        { !server && (
                            <li>
                                <NavLink activeClassName={ Styles.activeMenu } to={ book.login }>Увійти</NavLink>
                            </li>
                        ) }

                        { server && (
                            <li>
                                <div onClick={ () => logout() }>Вийти</div>
                            </li>
                        ) }
                        { !server && (
                            <li>
                                <NavLink activeClassName={ Styles.activeMenu } to={ book.register }>Реєстрація</NavLink>
                            </li>
                        ) }
                    </ul>
                </div>
            </header>
        );
    }
}

export const Header = withRouter(HeaderComponent);
