/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package network

import (
	"encoding/json"
	"fmt"
	"log"
	"net/url"

	"../models"
	"../player"
	"github.com/gorilla/websocket"
)

type Client struct {
	Url string
	Id  int
}

func NewClient(id int, url string) *Client {
	c := &Client{Url: url, Id: id}

	go c.run()

	return c
}

func (c *Client) run() {

	u, err := url.Parse(c.Url)
	if err != nil {
		panic(err)
	}

	log.Printf("connecting to %s", u.String())

	conn, _, err := websocket.DefaultDialer.Dial(u.String(), nil)
	if err != nil {
		log.Fatal("dial:", err)
	}

	player := player.NewPlayer(c.Id)
	go func() {
		defer conn.Close()
		for {
			_, message, err := conn.ReadMessage()
			if err != nil {
				log.Println("read:", err)
				return
			}

			turnInfo := models.TurnInfo{}
			json.Unmarshal(message[6:], &turnInfo)

			t := player.MakeTurn(&turnInfo)

			log.Println("answer: " + t)

			msg := fmt.Sprintf("%s", t)
			conn.WriteMessage(websocket.TextMessage, []byte(msg))
		}
	}()
}
