# NetLogo Vid Extension

## Running

Requires a 5.3 preview release of NetLogo.

## Building

Build with `sbt package`. `sbt test` runs tests.

## Concepts

### Video Source

The `vid` extension has a built in concept of a video source.
At the moment, the only video sources available are movies in the directory the model lives in and cameras attached to the computer.
The `vid` extension opens a new video source with the `vid:<source>-open` and `vid:<source>-select`.
These primitives change the source to the selected source.
If a source is already open, it closes it before opening a new one.

### Source Lifecycle

Movie sources are "stopped" after being created by `vid:movie-select` or `vid:movie-open`.
Camera sources start off as "playing" after being created by `vid:camera-select` or `vid:camera-open`.
If a source is in status "stopped" it can be started with `vid:start`.
Conversely, if the source is "playing" it can be stopped with `vid:stop`.
When a source is "stopped", each call to `vid:capture-image` will return the same image.

## Primitives

### `vid:camera-select`

Prompts the user to select a camera as video source. This command does not error if the user cancels. Use `vid:status` to see if a user selected a camera.

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
vid:camera-open ; opens first camera
vid:camera-open "Logitech Camera"
```

Errors:

* Message `"vid: no cameras found"`: no cameras are available.
* Message `"vid: camera "\<name\>" not found"`: if the named camera is not available.

### `vid:movie-select`

Prompts the user to select a movie to use as a video source.
The formats supported are those [supported by JavaFX2](https://docs.oracle.com/javafx/2/api/javafx/scene/media/package-summary.html#SupportedMediaTypes).
This command does not error if the user cancels.
Use `vid:status` to see if the user selected a movie.

Example:

```NetLogo
vid:movie-select
```

Errors:

* Message `"vid: format not supported"`: the user selected a movie with an unsupported format.

### `vid:movie-open`

Opens a video from the given path relative to the current model directory.

Example:

```NetLogo
vid:movie-open "foo.mp4"
```

Errors:

* Message `"vid: no movie found"`: the movie could not be found.
* Message `"vid: format not supported"`: the user selected a movie with an unsupported format.

### `vid:close`

Closes the currently selected video source.
Has no effect if there is no active video source.

Example:

```NetLogo
vid:close
```

### `vid:start`

Starts the selected video source.
A video source must have been selected before calling `vid:start`.

Example:

```NetLogo
vid:start
```

Errors:

* Message `"vid: no selected source"`: There is no currently selected video source. Select a source with `vid:movie-open`, `vid:movie-select`, `vid:camera-open`, or `vid:camera-select`.

### `vid:stop`

Stops the currently running video source.

Example:
```NetLogo
vid:stop
```

### `vid:status`

Reports the current status of an active video.
Note that calling `vid:movie-open` or `vid:movie-select` the status will be set to "stopped",
while after calling `vid:camera-open` or `vid:camera-select` the status will be "playing".

Example:

```NetLogo
vid:status     ; => "inactive"

vid:movie-open "foobar.mp4"
vid:status      ; => "stopped"

vid:movie-start
vid:status       ; => "playing"
```

### `vid:capture-image *width* *height*`

Captures an image from the currently selected active source.

Example:

```NetLogo
; when camera open, take an image
vid:capture-image ;=> returns image suitable for use with bitmap extension

; capture an image if a video source is open,
; have the user select a camera if no video source found
carefully [
  vid:capture-image 640 480
] [
  if errormessage = "vid: no selected source" [
    vid:camera-select
    vid:start
    vid:capture-image 640 480
  ]
]
```

Errors:

* Message `"vid: no selected source"`: There is no currently selected video source. Select a source with `vid:movie-open`, `vid:movie-select`, `vid:camera-open`, or `vid:camera-select`.
* Message `"vid: invalid dimensions"`: The selected dimensions are invalid (one of the dimensions is zero or negative).

### `vid:set-time *seconds*`

Sets the time of the current video source to `*seconds*`.
This has no effect when the current video source is a camera.

Example:
```NetLogo
vid:set-time 100
```

Errors:

* Message `"vid: no selected source"`: There is no currently selected video source. Select a source with `vid:movie-open`, `vid:movie-select`, `vid:camera-open`, or `vid:camera-select`.
* Message `"vid: invalid time"`: The currently active video does not contain the specified second. The second may be negative, or greater than the length of the video.

### `vid:show-player *width* *height*`

Shows a player in a separate window.
If there is no video source, the window will be an empty black frame.
If there is an active video source, it will be displayed in the window with the specified width and height.
If there is a playing video source, it will be displayed in the window at its specified width and height.
If width and height are omitted, the video will be displayed in its native resolution.

Example:

```NetLogo
vid:show-player 640 480
```

Errors:

* Message `"vid: invalid dimensions"`: The selected dimensions are invalid (one of the dimensions is zero or negative).

### `vid:hide-player`

Hides the player if open. Does nothing if there is no player window.

Example:

```NetLogo
vid:hide-player
```

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Vid Extension is in the public domain.  To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.

