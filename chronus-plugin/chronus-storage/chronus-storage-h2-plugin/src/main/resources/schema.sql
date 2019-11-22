create table if not exists environment_info (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  cluster VARCHAR(250) NOT NULL,
  cluster_desc VARCHAR(250) DEFAULT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);

create table if not exists job_exec_log (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  cluster VARCHAR(250) NOT NULL,
  sys_code VARCHAR(250) NOT NULL,
  task_name VARCHAR(250) NOT NULL,
  exec_address VARCHAR(250) NOT NULL,
  req_no VARCHAR(250) NOT NULL,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP NOT NULL,
  handle_total_count VARCHAR(100) DEFAULT NULL,
  handle_fail_count VARCHAR(100) DEFAULT NULL,
  handle_detail VARCHAR(250) NOT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);


create table if not exists node_data_info (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  cluster VARCHAR(250) NOT NULL,
  tag VARCHAR(250) NOT NULL,
  master_address VARCHAR(250) NOT NULL,
  master_data_version VARCHAR(250) NOT NULL,
  executor_address VARCHAR(250) NOT NULL,
  executor_data_version VARCHAR(250) NOT NULL,
  executor_state VARCHAR(10) NOT NULL,
  executor_phase VARCHAR(50) NOT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);

create table if not exists node_info (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  cluster VARCHAR(250) NOT NULL,
  state VARCHAR(10) NOT NULL,
  address VARCHAR(250) NOT NULL,
  version VARCHAR(250) NOT NULL,
  host_name VARCHAR(250) NOT NULL,
  is_master VARCHAR(5) NOT NULL,
  is_executor VARCHAR(5) NOT NULL,
  heartbeat_time TIMESTAMP NOT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);

create table if not exists system_group_info (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  group_name VARCHAR(250) NOT NULL,
  sys_code VARCHAR(250) NOT NULL,
  sys_desc VARCHAR(250) NOT NULL,
  group_desc VARCHAR(250) NOT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);


create table if not exists tag_assign_info (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  cluster VARCHAR(250) NOT NULL,
  tag VARCHAR(250) NOT NULL,
  address_list VARCHAR(2500) NOT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);

create table if not exists tag_info (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  tag VARCHAR(50) NOT NULL,
  remarks VARCHAR(50) DEFAULT NULL,
  executor_num INTEGER NOT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);

create table if not exists task_info (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  tag VARCHAR(250) NOT NULL,
  cluster VARCHAR(250) NOT NULL,
  task_name VARCHAR(250) NOT NULL,
  remark VARCHAR(250) NOT NULL,
  heart_beat_rate INT NOT NULL,
  judge_dead_interval INT NOT NULL,
  sleep_time_no_data FLOAT NOT NULL,
  sleep_time_interval FLOAT NOT NULL,
  fetch_data_number INT NOT NULL,
  execute_number INT NOT NULL,
  thread_number INT NOT NULL,
  processor_type VARCHAR(250) NOT NULL,
  permit_run_start_time VARCHAR(250) NOT NULL,
  permit_run_end_time VARCHAR(250) NOT NULL,
  deal_bean_name VARCHAR(250) NOT NULL,
  deal_sys_code VARCHAR(250) NOT NULL,
  deal_biz_bean_name VARCHAR(250) NOT NULL,
  task_parameter VARCHAR(250) NOT NULL,
  task_items VARCHAR(250) NOT NULL,
  force_cron_exec VARCHAR(250) NOT NULL,
  assign_num INTEGER NOT NULL,
  state VARCHAR(250) NOT NULL,
  is_broadcast_invoker VARCHAR(250) NOT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);

create table if not exists task_runtime_info (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  cluster VARCHAR(250) NOT NULL,
  task_name VARCHAR(250) NOT NULL,
  address VARCHAR(250) NOT NULL,
  host_name VARCHAR(250) NOT NULL,
  register_time VARCHAR(250) NOT NULL,
  heart_beat_time VARCHAR(250) NOT NULL,
  last_fetch_data_time TIMESTAMP NOT NULL,
  next_run_start_time VARCHAR(250) NOT NULL,
  next_run_end_time VARCHAR(250) NOT NULL,
  version VARCHAR(250) DEFAULT NULL,
  taskItems VARCHAR(250) DEFAULT NULL,
  state VARCHAR(250) DEFAULT NULL,
  date_created TIMESTAMP NOT NULL,
  created_by VARCHAR(250) NOT NULL,
  date_updated TIMESTAMP NOT NULL,
  update_by VARCHAR(250) NOT NULL
);