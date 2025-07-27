cd common/src/main/resources/assets/inkomancy/textures

names=('white' 'orange' 'magenta' 'light_blue' 'yellow' 'lime' 'pink' 'gray' 'light_gray' 'cyan' 'purple' 'blue' 'brown' 'green' 'red' 'black')
hex=('#F9FFFE' '#F9801D' '#C74EBD' '#3AB3DA' '#FED83D' '#80C71F' '#F38BAA' '#474F52' '#9D9D97' '#169C9C' '#8932B8' '#3C44AA' '#835432' '#5E7C16' '#B02E26' '#1D1D21')

for item in 'ardent' 'conductive'; do
  for idx in "${!names[@]}"; do
    echo "Processing item/${names[$idx]}_${item}_ink.png"
    magick "item/${item}_ink.png" -colorspace gray -fill "${hex[$idx]}" -tint 150 "item/${names[$idx]}_${item}_ink.png"

    for type in 'corner' 'dot' 'end' 'four' 'straight' 'three'; do
      echo "Processing block/${names[$idx]}_${item}_ink_${type}.png"
      magick "block/${item}_ink_${type}.png" -colorspace gray -fill "${hex[$idx]}" -tint 150 "block/${names[$idx]}_${item}_ink_${type}.png"
    done
  done
done


