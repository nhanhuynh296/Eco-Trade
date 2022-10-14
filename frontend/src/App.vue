<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
const app = {
  name: "app"
};

export default app;
</script>

<style lang="scss">
// Import Bulma's core
@import "~bulma/sass/utilities/_all";

// Set your colors
$primary: #9dac8e;
$primary-light: findLightColor($primary);
$primary-dark: findDarkColor($primary);
$primary-invert: findColorInvert($primary);
$twitter: #4099FF;
$twitter-invert: findColorInvert($twitter);
$webyellow: #e9c46a;

// Lists and maps
$custom-colors:
    (
        "webyellow": (
            $webyellow
        )
    );
$custom-shades: null !default;

// Setup $colors to use as bulma classes (e.g. 'is-twitter')
$colors: mergeColorMaps(
        (
            "white": (
                $white,
                $black,
            ),
            "black": (
                $black,
                $white,
            ),
            "light": (
                $light,
                $light-invert,
            ),
            "dark": (
                $dark,
                $dark-invert,
            ),
            "primary": (
                $primary,
                $primary-invert,
                $primary-light,
                $primary-dark,
            ),
            "link": (
                $link,
                $link-invert,
                $link-light,
                $link-dark,
            ),
            "info": (
                $info,
                $info-invert,
                $info-light,
                $info-dark,
            ),
            "success": (
                $success,
                $success-invert,
                $success-light,
                $success-dark,
            ),
            "warning": (
                $warning,
                $warning-invert,
                $warning-light,
                $warning-dark,
            ),
            "danger": (
                $danger,
                $danger-invert,
                $danger-light,
                $danger-dark,
            ),
        ),
        $custom-colors
);

// Links
$link: $primary;
$link-invert: $primary-invert;
$link-focus-border: $primary;

// Panels
//$panel-heading-blue: ;

//$panel-heading-

// Import Bulma and Buefy styles
@import "~bulma";
@import "~buefy/src/scss/buefy";
</style>
