<template>
  <div>
    <p class="send-msg-label">
      <b-icon icon="message-text" custom-size="1"/>
      {{ !fromFeed ? "Send message to seller..." : "Reply..." }}
    </p>
    <b-field class="send-msg-field">
      <b-input v-model="sendMessageInput"
               icon-right="close-circle" :disabled="sentMessage"
               :icon-right-clickable="!sentMessage" @icon-right-click="sendMessageInput = ''"
               type="textarea" class="send-msg-input" size="is-small" rounded
      ></b-input>
    </b-field>
    <transition name="fade" mode="out-in">
      <div key="1" v-if="!sentMessage" class="full-width">
        <b-button id="send-btn" type="is-info is-light" expanded size="is-small" @click="sendMessage">
          Send
        </b-button>
      </div>
      <div key="2" v-else class="full-width">
        <b-button type="is-success" id="msg-success-btn" expanded disabled=""
                  size="is-small">
          <div class="is-inline-flex">
            <p id="message-sent-label">Message Sent</p>
            <b-icon icon="check"/>
          </div>
        </b-button>
      </div>
    </transition>
  </div>
</template>

<script>
import api from "../../../src/api/api";
import logger from "../../logger";

export default {
  name: "SendMessageComponent",
  props: ['cardId', 'recipientId', 'fromFeed'],

  data() {
    return {
      sendMessageInput: '',
      sentMessage: false,
    }
  },

  methods: {


    /**
     * Prevents the user from pressing enter in message input to start new line
     */
    onSubmit(event) {
      event.preventDefault();
      this.sendMessage();
    },

    /**
     * Send a message to the card creator
     */
    sendMessage() {
      if (this.sendMessageInput.length > 0) {
        let payload = {
          recipientId: this.recipientId,
          cardId: this.cardId,
          message: this.sendMessageInput
        }
        api.notifications.postMessage(payload)
        .then(() => {
          this.sentMessage = true;
        })
        .catch(error => {
          logger.getLogger().warn(error)
        });
      }
    }
  }
}
</script>
