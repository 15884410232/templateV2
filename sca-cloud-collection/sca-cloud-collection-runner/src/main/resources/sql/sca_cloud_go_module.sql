CREATE TABLE public.sca_cloud_go_module (
	id varchar NOT NULL,
	"name" varchar NULL,
	"version" varchar NULL,
	release_time timestamp NULL,
	create_time timestamp NULL,
	update_time timestamp NULL,
	task_id varchar NULL
);
CREATE INDEX sca_cloud_go_moudle_id_idx ON public.sca_cloud_go_module (id);
CREATE INDEX sca_cloud_go_moudle_name_idx ON public.sca_cloud_go_module ("name");
CREATE INDEX sca_cloud_go_module_release_time_idx ON public.sca_cloud_go_module (release_time);
CREATE INDEX sca_cloud_go_module_task_id_idx ON public.sca_cloud_go_module (task_id);

-- Column comments

COMMENT ON COLUMN public.sca_cloud_go_module.id IS 'id';
COMMENT ON COLUMN public.sca_cloud_go_module."name" IS '模块名';
COMMENT ON COLUMN public.sca_cloud_go_module."version" IS '版本';
COMMENT ON COLUMN public.sca_cloud_go_module.release_time IS '发布时间';
COMMENT ON COLUMN public.sca_cloud_go_module.task_id IS '来源任务id';
