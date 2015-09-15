# NetLogo Vid Extension

## Primitives

### `vid:camera-select`

Selects a camera for capturing video. Should include a width and height with which to capture.

Example:
```NetLogo
vid:camera-select world-width world-height
```

### `vid:camera-start`

Starts the default camera (will vary depending on the system) at the specified width and height.

Example:
```NetLogo
vid:camera-start world-width world-height
```

### `vid:camera-image`

Captures the image on the currently active camera. If no camera is active, this primitive will raise an Extension Exception with the errormessage "vid: no camera open".

Example:
```NetLogo
; when camera open, take an image
vid:camera-image ; returns image suitable for use with bitmap extension

; capture an image if the camera is open, have the user
; select a camera if no camera is open
carefully [
  vid:camera-image
] [
  if errormessage = "vid: no camera open" [
    vid:camera-select
    vid:camera-image
  ]
]
```

### `vid:camera-stop`

Stops the currently active camera. This primitive will not error if no camera is open.

Example:
```NetLogo
vid:camera-stop
```

### `vid:movie-open`

Opens the specified movie.

### `vid:movie-start`

### `vid:movie-image`

### `vid:movie-stop`

### `vid:movie-open-player`

### `vid:movie-set-time`

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Extension Activator Template is in the public domain.  To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.

