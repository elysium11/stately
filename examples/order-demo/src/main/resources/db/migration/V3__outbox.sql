CREATE TABLE IF NOT EXISTS outbox (
  id            bigserial primary key,
  aggregate_type varchar(50) not null,
  aggregate_id   varchar(50) not null,
  event_type     varchar(100) not null,
  payload        jsonb not null,
  operation_id   varchar(100),
  status         varchar(20) not null default 'NEW',
  created_at     timestamp not null default now()
);

CREATE INDEX IF NOT EXISTS outbox_status_created_idx ON outbox(status, created_at);
CREATE UNIQUE INDEX IF NOT EXISTS outbox_operation_id_idx ON outbox(operation_id) WHERE operation_id IS NOT NULL;
