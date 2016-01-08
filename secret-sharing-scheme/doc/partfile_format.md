# secret sharing scheme: partfile format

```
partfile:
	"SSSSSSSS"
	length=<int64,non-negative>
	garbage=<int64,random>
	version_major=<int64,non-negative>
	version_minor=<int64,non-negative>
	meta_count=<int64,non-negative> /* count does not include secretpart */
	meta=<meta{meta_count}>
	secretpart=<secretpart>
	;

meta:
	( timestamp_meta | freeform_meta )
;

timestamp_meta:
	meta_id=<int64=1>
	meta_length=<int64=8>
	timestamp_millis_utc=<int64>
;

freeform_meta:
	meta_id=<int64=-1>
	meta_length=<int64,non-negative>
	bytes=bytes{meta_length}
;

secret_part:
	meta_id=<int64=0>
	meta_length=<int64,non-negative>
	parameter_count=<int64,non-negative>
	parameters=<parameter{parameter_count}>
	composition_count=<int64,non-negative>
	composition=<secret_part{composition_count}>
;

parameter:
	algorithm_parameter

```
