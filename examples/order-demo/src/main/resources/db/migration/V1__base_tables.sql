create table if not exists fsm_transition (
  id bigserial primary key,
  aggregate_type text not null,
  aggregate_id   text not null,
  from_state     text not null,
  to_state       text not null,
  event          text not null,
  created_at     timestamptz not null default now(),
  metadata       jsonb,
  uniq_key       text,
  constraint uq_fsm_uniq unique (aggregate_type, aggregate_id, uniq_key)
);

create index if not exists ix_fsm_transition_agg
  on fsm_transition(aggregate_type, aggregate_id, created_at);