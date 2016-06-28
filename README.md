# NetLogo Vid Extension

## Running

Requires a 6.0 preview release of NetLogo.

## Building

Build with `sbt package`. `sbt test` runs tests.

## Concepts

### Video Source

The `vid` extension has a built-in concept of a video source.
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

### Video Recorder

The `vid` extension also has the concept of a recording, a series of frames which can be sewn into an "mp4" movie.
The recorder status can be queried using `vid:recorder-status`.
The recorder status is "inactive" until started with `vid:start-recorder`, which sets it to "recording".
While the recorder is "recording" the `vid:record-view`, `vid:record-interface`, and `vid:record-source` can be used to save frames to the recording.
You can choose to save the recording while recording using `vid:save-recording` which saves the movie to the specified file and reset the recording status to "inactive".
If you would prefer to throw away the recorded frames without saving, use `vid:reset-recorder`.

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
### <tt>(vid:camera-open <i>camera-name</i>)</tt>

Opens the named camera as a video source.
If no name is provided, opens the first camera that would be listed by `camera-names`.

Example:
```NetLogo
vid:camera-open ; opens first camera
(vid:camera-open "Logitech Camera")
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

Opens a video from the file system.
If the provided path is not absolute the extension searches for the given path relative to the current model directory.
If the provided path is absolute the extension opens the file.

Example:

```NetLogo
vid:movie-open "foo.mp4"      ; Opens foo.mp4 in the directory containing the model
vid:movie-open user-file      ; Opens a dialog for the user to select a movie
vid:movie-open "/tmp/foo.mp4" ; Opens a movie from the "/tmp" directory
```

Errors:

* Message `"vid: no movie found"`: the movie could not be found.
* Message `"vid: format not supported"`: the user selected a movie with an unsupported format.

### `vid:movie-open-remote`

Opens a remote video from a website or ftp server.

Example:

```NetLogo
vid:movie-open-remote "http://example.org/foo.mp4"
```

Errors:

* Message `"vid: no movie found"`: The specified URL could not be loaded or errored while loading.
* Message `"vid: format not supported"`: The file type of the remote movie is not supported.
* Message `"vid: protocol not supported"`: The movie was at an upsupported URL protocol. Supported protocols are `ftp` and `http`.

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
Note that after calling `vid:movie-open` or `vid:movie-select` the status will be set to "stopped",
while after calling `vid:camera-open` or `vid:camera-select` the status will be "playing".

Example:

```NetLogo
vid:status     ; => "inactive"

vid:movie-open "foobar.mp4"
vid:status      ; => "stopped"

vid:movie-start
vid:status       ; => "playing"
```


### `vid:capture-image`
### <tt>(vid:capture-image <i>width height</i>)</tt>

Captures an image from the currently selected active source.

If width and height are not specified, the image is captured at the current source resolution.

Example:

```NetLogo
extensions [ vid bitmap ]

to capture
  ; capture an image if a video source is open,
  ; have the user select a camera if no video source found
  carefully [
    ; when camera open, take an image
    let image vid:capture-image ; returns image suitable for use with bitmap extension
    bitmap:copy-to-drawing image 0 0
  ] [
    if error-message = "Extension exception: vid: no selected source" [
      vid:camera-select
      vid:start
      let image vid:capture-image
      bitmap:copy-to-drawing image 0 0
    ]
  ]
end
```

If you want to capture images at a different resolution, simply replace `vid:capture-image` with, e.g., `(vid:capture-image 640 480)`.


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

### `vid:show-player`
### <tt>(vid:show-player <i>width height</i>)</tt>

Shows a player in a separate window.
If there is no video source, the window will be an empty black frame.
If there is an active video source, it will be displayed in the window with the specified width and height.
If there is a playing video source, it will be displayed in the window at its specified width and height.
If width and height are omitted, the video will be displayed in its native resolution.

Example with native resolution:

```NetLogo
vid:show-player
```

Example with custom resolution:

```NetLogo
(vid:show-player 640 480)
```

Errors:

* Message `"vid: invalid dimensions"`: The selected dimensions are invalid (one of the dimensions is zero or negative).

### `vid:hide-player`

Hides the player if open. Does nothing if there is no player window.

Example:

```NetLogo
vid:hide-player
```

### `vid:record-view`

Records the current image shown in the NetLogo view to the active recording.

Example:

```NetLogo
vid:record-view
```

Errors:

* Message `"vid: recorder not started"`: The recorder has not been started. Start the recorder with `vid:start-recorder`.

### `vid:record-interface`

Records the NetLogo interface view to the active recording.

Example:

```NetLogo
vid:record-interface
```

Errors:

* Message `"vid: recorder not started"`: The recorder has not been started. Start the recorder with `vid:start-recorder`.
* Message `"vid: export interface not supported"`: The calling NetLogo version does not support interface exports. This will occur when running NetLogo headlessly.

### `vid:record-source`

Records a frame to the active recording from the currently active source.

Example:

```NetLogo
vid:record-source
```

Errors:

* Message `"vid: recorder not started"`: The recorder has not been started. Start the recorder with `vid:start-recorder`.
* Message `"vid: no selected source"`: There is no currently selected video source. Select a source with `vid:movie-open`, `vid:movie-select`, `vid:camera-open`, or `vid:camera-select`.

### `vid:recorder-status`

Reports the current status of the recorder.
Initially and after the recorder is saved (via `vid:save-recording`) or reset (via `vid:reset-recorder`) the recorder status is "inactive".
After calling `vid:start-recorder` the status will be "recording".

Example:

```NetLogo
vid:recorder-status ; => "inactive"

vid:start-recorder
vid:recorder-status ; => "recording"

vid:reset-recorder
vid:recorder-status ; => "inactive"
```

### `vid:start-recorder`

Starts the recorder.
If the recorder is already running this will cause an error to be raised.
If desired, a recording width and height can be supplied.
If height and width are not supplied, they will be determined from the first frame recorded.

Example:

```NetLogo
(vid:start-recorder)
(vid:start-recorder 640 480)
```

Errors:

* Message `"vid: recorder already started"`: The recorder has already been started. The existing recording should be saved or reset before starting the recording.
* Message `"vid: invalid dimensions"`: The selected dimensions are invalid (one of the dimensions is zero or negative).

### `vid:save-recording`

Saves the recording to the specified path.
If the recorder is not running this will cause an error to be raised.
Note that at present the recording will always be saved in the "mp4" format.
If the supplied filename does not end in ".mp4", the ".mp4" suffix will be added.
Note that `vid:save-recording` *will* overwrite existing files of the same name.
`vid:save-recording` will error if the recorder has not been started or if the file cannot be written since the containing directory does not exist.

Example:

```NetLogo
vid:save-recording "foo.mp4"      ; Saves to foo.mp4 in the directory containing the model
vid:save-recording user-new-file  ; Opens a dialog for the user to select a save path
vid:save-recording "/tmp/foo.mp4" ; Saves the recording to the "/tmp" directory
```

Errors:

* Message `"vid: recorder not started"`: The recorder has not been started. Start the recorder with `vid:start-recorder`.
* Message `"vid: no such directory"`: The directory containing the specified save file does not exist.

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Vid Extension is in the public domain.  To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.

