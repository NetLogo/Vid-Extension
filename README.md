# NetLogo Vid Extension

## Primitives

### `vid:camera-select`

Prompts the user to select a camera as video source

Example:
```NetLogo
vid:camera-select
```

Errors:

* Message "vid: no cameras found": no cameras are available.

### `vid:camera-names`

Provides a list of all available cameras.

Example:
```NetLogo
vid:camera-names => []
vid:camera-names => ["Mac Camera"]
vid:camera-names => ["Logitech Camera"]
```

### `vid:camera-open`

Opens the named camera as a video source.
If no name is provided, opens the first camera that would be listed by `camera-names`.

Example:
```NetLogo
vid:camera-start ; opens first camera
vid:camera-start "Logitech Camera"
```

Errors:

* Message `"vid: no cameras found"`: no cameras are available.
* Message `"vid: camera "\<name\>" not found"`: if the named camera is not available.

### `vid:movie-select`

Prompts the user to select a movie to use as a video source.
The formats supported are those [supported by JavaFX2](https://docs.oracle.com/javafx/2/api/javafx/scene/media/package-summary.html#SupportedMediaTypes).

Example:

```NetLogo
vid:movie-select
```

Errors:

* Message `"vid: no movie selected"`: the user did not select a movie.
* Message `"vid: format not supported"`: the user selected a movie with an unsupported format.

### `vid:movie-open`

Prompts the user to select a movie to use as a video source.
The formats supported are those [supported by JavaFX2](https://docs.oracle.com/javafx/2/api/javafx/scene/media/package-summary.html#SupportedMediaTypes).

Example:

```NetLogo
vid:movie-open "foo.mp4"
```

Errors:

* Message `"vid: no movie found"`: the movie could not be found.
* Message `"vid: format not supported"`: the user selected a movie with an unsupported format.

### `vid:close`

Closes the currently selected video source.
Errors if the source has not been stopped with `vid:stop`.
Has no effect if there is no active video source.

Example:

```NetLogo
vid:close
```

Errors:

* Message `"vid: video in progress"`: The active source is currently running, stop it first with `vid:stop`

### `vid:start *width* *height*`

Starts the selected video source with the given width and height.
A video source must have been selected before being started.
This must be called before `vid:capture-image` or `vid:set-time`.

Example:

```NetLogo
vid:start 640 480
```

Errors:

* Message `"vid: no selected source"`: There is no currently selected video source. Select a source with `vid:movie-open`, `vid:movie-select`, `vid:camera-open`, or `vid:camera-select`.
* Message `"vid: invalid dimensions"`: The selected dimensions are invalid (one of the dimensions is zero or negative).

### `vid:stop`

Stops the currently running video source.

Example:
```NetLogo
vid:stop
```

### `vid:capture-image`

Captures an image from the currently selected active source.

Example:

```NetLogo
; when camera open, take an image
vid:camera-image ; returns image suitable for use with bitmap extension

; capture an image if the camera is open, have the user
; select a camera if no camera is open
carefully [
  vid:camera-image
] [
  if errormessage = "vid: no selected source" [
    vid:camera-select
    vid:start 640 480
    vid:capture-image
  ]
]
```

Errors:

* Message `"vid: no selected source"`: There is no currently selected video source. Select a source with `vid:movie-open`, `vid:movie-select`, `vid:camera-open`, or `vid:camera-select`.
* Message `"vid: source stopped"`: The currently active video source has not been started. Start it with `vid:start`.

### `vid:set-time *seconds*`

Sets the time of the current video source to `*seconds*`.
This has no effect when the current video source is a camera.

Example:
```NetLogo
vid:set-time 100
```

Errors:

* Message `"vid: no selected source"`: There is no currently selected video source. Select a source with `vid:movie-open`, `vid:movie-select`, `vid:camera-open`, or `vid:camera-select`.
* Message `"vid: source stopped"`: The currently active video source has not been started. Start it with `vid:start`.
* Message `"vid: invalid time"`: The currently active video does not contain the specified second. The second may be negative, or greater than the length of the video.

### `vid:show-player`

Shows a player in a separate window.
If there is no video source, the window will be an empty black frame.
If there is an active video source, it will be displayed in the window at its native resolution.
If there is a playing video source, it will be displayed in the window at its playback resolution.

Example:

```NetLogo
vid:show-player
```

### `vid:hide-player`

Hides the player if open. Works just as though the player window had been closed.

Example:

```NetLogo
vid:hide-player
```

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Extension Activator Template is in the public domain.  To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.

