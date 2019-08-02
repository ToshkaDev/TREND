#!/bin/bash
# https://stackoverflow.com/questions/30332137 with my (Vadim) changes
# The script allows to run several xvfb-run instances in parallel avoiding race conditions.
xvfb_run=$1

# allow settings to be updated via environment
: "${xvfb_lockdir:=$HOME/.xvfb-locks}"
: "${xvfb_display_min:=99}"
: "${xvfb_display_max:=599}"

# assuming only one user will use this, let's put the locks in our own home directory
# avoids vulnerability to symlink attacks.
mkdir -p -- "$xvfb_lockdir" || exit

i=$xvfb_display_min     # minimum display number
while (( i < xvfb_display_max )); do
  if [ -f "/tmp/.X$i-lock" ]; then                # still avoid an obvious open display
    (( ++i )); continue
  fi
  exec 5>"$xvfb_lockdir/$i" || continue           # open a lockfile
  if flock -x -n 5; then                          # try to lock it; "${@:2}" means get position parameters starting from the second one
    exec $xvfb_run --server-num="$i" "${@:2}" || exit  # if locked, run xvfb-run
  fi
  (( i++ ))
done
