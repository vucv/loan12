#!/bin/bash
# encode WAV/AMR to OGG with linear loudness gain (no time-stretching) - keeps loop lengths exact
set -e
in="$1"; out="$2"; rate="$3"; ch="$4"
I=$(ffmpeg -hide_banner -nostats -i "$in" -af ebur128 -f null - 2>&1 | grep -E "^\s+I:" | tail -1 | awk '{print $2}')
TP=$(ffmpeg -hide_banner -nostats -i "$in" -af "volumedetect" -f null - 2>&1 | grep max_volume | awk '{print $5}')
gain=$(python3 -c "
I=float('$I'); tp=float('$TP')
g=-16.0-I
g=min(g, -1.0-tp)   # do not clip
print(round(g,2))")
ffmpeg -hide_banner -loglevel error -y -i "$in" -af "volume=${gain}dB" -ar $rate -ac $ch -c:a libvorbis -q:a 5 "$out"
echo "$(basename $out): I=$I tp=$TP gain=${gain}dB"
