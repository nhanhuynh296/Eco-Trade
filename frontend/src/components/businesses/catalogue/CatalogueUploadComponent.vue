<template>
  <div>
    <b-field>
      <b-upload v-model="dropFiles" multiple drag-drop expanded accept=".jpg, .png, .jpeg">
        <section class="section">
          <div class="content has-text-centered" style="max-width: 100%;">
            <p>
              <b-icon icon="upload" size="is-medium"></b-icon>
            </p>
            <p>Drop your files here or click to upload</p>
            <p><small>(max 10, with max size 1mb each)</small></p>
          </div>
        </section>
      </b-upload>
    </b-field>

    <div class="tags" style="max-width: 40vh;">
        <span v-for="(file, index) in dropFiles" :key="index" class="tag" style="background: #A4CAEB; color: #fff;">
          {{file.name}}

          <button id="delete-drop-file-button" class="delete is-small" type="button" @click="deleteDropFile(index)"></button>
        </span>
    </div>
  </div>
</template>

<script>
import error from "../../../components/errorPopup";

export default {
  name: "CatalogueUploadComponent",

  data() {
    return {
      dropFiles: [],
      errors: [],
    }
  },

  watch: {
    /**
     * Removes files that are too large or if more than 10 are uploaded.
     */
    dropFiles: function (val) {
      // List of items to remove (by index)
      const indices = [];

      for (let i = 0; i < val.length; i++) {
        // 1048576 is the max size accepted by backend
        if (val[i].size > 1048576) {
          this.errors.push(`The file '${val[i].name}' is greater than 1mb!`);
          // Store index of the large file for removal below
          indices.push(i);
        }
      }

      // Remove the files that are larger than 1mb
      val = val.filter((_, index) => !indices.includes(index));

      if (val.length > 10) {
        this.errors.push('You may upload at most 10 files!');
        val.splice(10, this.dropFiles.length - 10);
      }

      if (this.errors.length !== 0) {
        error.showPopup(this, this.errors);
        this.errors = [];
        this.dropFiles = val;
      }

      this.$parent.updateDropFiles(this.dropFiles);
    }
  },

  methods: {
    /**
     * Deletes a file from the dropFiles data list at a certain index
     */
    deleteDropFile(index) {
      this.dropFiles.splice(index, 1);
    }
  }
}
</script>
